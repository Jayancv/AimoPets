import { useState, useEffect } from "react";
import UserCard from "./UserCard";
import type { User } from "../models/User";
import "./UserBoard.css";

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
    console.log("User data changed, reset to 1st page");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    setCurrentPage(1);
  }, [users]);

  if (loading) {
    return <p className="user-board-message">Loading...</p>;
  }

  if (error) {
    return <p className="user-board-error">{error}</p>;
  }

  if (users.length === 0) {
    return (
      <p className="user-board-empty">No users found. Try fetching!</p>
    );
  }

  return (
    <div>
      <div className="user-board-grid">
        {currentUsers.map((user) => (
          <UserCard key={user.id} user={user} />
        ))}
      </div>

      {/* Pagination controls */}
      {totalPages > 1 && (
        <div  className="user-board-pagination">
          <button
            name="previous"
            onClick={handlePrev}
            disabled={currentPage === 1}
            className="user-board-button"
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
            className="user-board-button"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default UserBoard;
