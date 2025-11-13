import { useState, useEffect } from "react";
import UserCard from "./UserCard";
import type { User } from "../models/User";

interface UserBoardProps {
  users: User[];
  loading: boolean;
  error: string | null;
  itemsPerPage?: number;
}

const UserBoard = ({ users, loading, error,  itemsPerPage = 25  }: UserBoardProps) => {

  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = Math.ceil(users.length / itemsPerPage);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentUsers = users.slice(startIndex, startIndex + itemsPerPage);

  const handlePrev = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  const handleNext = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  useEffect(() => {
    setCurrentPage(1);
  }, [users]);

  if (loading) {
    return <p className="text-center text-gray-500">Loading...</p>;
  }

  if (error) {
    return <p className="text-center text-red-500">{error}</p>;
  }

  if (users.length === 0) {
    return <p className="text-center text-gray-400">No users found. Try fetching!</p>;
  }

  return (
    <div >
      <div className="flex flex-wrap justify-center gap-6">
      {currentUsers.map((user) => (
        <UserCard key={user.id} user={user} />
      ))}
    </div>

     {/* Pagination controls */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-4 mt-4">
          <button
            name="previous"
            onClick={handlePrev}
            disabled={currentPage === 1}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Previous
          </button>

          <span className="flex items-center">
            Page {currentPage} of {totalPages}
          </span>

          <button
            name="next"
            onClick={handleNext}
            disabled={currentPage === totalPages}
            className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default UserBoard;
