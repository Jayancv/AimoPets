import { useState } from "react";
import { fetchUsersWithPet } from "./services/userService";
import UserFilter from "./components/Filter";
import UserBoard from "./components/UserBoard";
import type { User } from "./models/User";
import "./App.css";

const App = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFilter = async (count: number, nat: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchUsersWithPet(count, nat);
      setUsers(data);
    } catch (err: unknown) {
      if (typeof err === "object" && err !== null && "response" in err && (err as any).response?.data?.error) {
        setError((err as any).response.data.error); // Use backend error message
      } else {
        setError("Failed to fetch users.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      <h1 className="app-title">Users with their Pets</h1>
      <p className="app-subtitle">Browse users from different countries and see their beloved pets.</p>
      <UserFilter onFilter={handleFilter} />
      <UserBoard users={users} loading={loading} error={error}  itemsPerPage={25}/>
    </div>
  );
};

export default App;
