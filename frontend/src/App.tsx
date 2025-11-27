import { useCallback, useRef, useState } from "react";
import { fetchUsersWithPet } from "./services/userService";
import UserFilter from "./components/Filter";
import UserBoard from "./components/UserBoard";
import type { User } from "./models/User";
import "./App.css";

const App = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // For canceling previous requests
  const cancelTokenRef = useRef<AbortController | null>(null);

  const handleFilter =  useCallback(async (count: number, nat: string) => {
    setLoading(true);
    setError(null);

    // Cancel previous request if it exists
    cancelTokenRef.current?.abort();
    const controller = new AbortController();
    cancelTokenRef.current = controller;

    try {
      const data = await fetchUsersWithPet(count, nat, controller.signal);
      setUsers(data);
    } catch (err: unknown) {
      console.log(err)
      if (err instanceof DOMException && err.name === "AbortError"){
        return; // canceled
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Failed to fetch users.");
      }
    } finally {
      setLoading(false);
    }
  }, []);

  return (
    <div className="app-container">
      <h1 className="app-title">Users with their Pets</h1>
      <p className="app-subtitle">Browse users from different countries and see their beloved pets.</p>
      <UserFilter onFilter={handleFilter} loading={loading} />
      <UserBoard users={users} loading={loading} error={error}  itemsPerPage={25}/>
    </div>
  );
};

export default App;
