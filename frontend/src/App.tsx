import { useState } from "react";
import { fetchUsersWithPet } from "./services/userService";
import UserFilter from "./components/Filter";
import UserBoard from "./components/UserBoard";
import type { User } from "./models/User";

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
    } catch (err: any) {
      if (err.response && err.response.data && err.response.data.error) {
        setError(err.response.data.error); // Use backend error message
      } else {
        setError("Failed to fetch users.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-2xl font-bold text-center mb-1">Users with their Pets</h1>
      <p className="text-center text-gray-600 mb-6">Browse users from different countries and see their beloved pets.</p>
      <UserFilter onFilter={handleFilter} />
      <UserBoard users={users} loading={loading} error={error}  itemsPerPage={25}/>
    </div>
  );
};

export default App;
