import { render, screen, fireEvent } from "@testing-library/react";
import UserBoard from "./UserBoard";
import type { User } from "../models/User";
import { describe, it, expect, vi } from "vitest";

// Mock 30 users
const mockUsers: User[] = Array.from({ length: 30 }, (_, i) => ({
  id: `${i + 1}`,
  gender: "male",
  country: "USA",
  name: `User ${i + 1}`,
  email: `user${i + 1}@mail.com`,
  dob: { date: "1990-01-01", age: 30 },
  phone: "123-456-7890",
  petImage: "",
}));

// Mock UserCard so we can test UserBoard independently
vi.mock("./UserCard", () => ({
  default: ({ user }: { user: User }) => <div data-testid="user-card">{user.name}</div>,
}));

describe("UserBoard Component", () => {
  it("renders loading state", () => {
    render(<UserBoard users={[]} loading={true} error={null} />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it("renders error state", () => {
    render(<UserBoard users={[]} loading={false} error="Something went wrong" />);
    expect(screen.getByText(/something went wrong/i)).toBeInTheDocument();
  });

  it("renders empty state when no users", () => {
    render(<UserBoard users={[]} loading={false} error={null} />);
    expect(screen.getByText(/no users found/i)).toBeInTheDocument();
  });

  it("renders first page with 25 users", () => {
    render(<UserBoard users={mockUsers} loading={false} error={null} itemsPerPage={25} />);
    const cards = screen.getAllByTestId("user-card");
    expect(cards.length).toBe(25);
    expect(cards[0]).toHaveTextContent("User 1");
    expect(cards[24]).toHaveTextContent("User 25");
  });

  it("pagination works correctly", () => {
    render(<UserBoard users={mockUsers} loading={false} error={null} itemsPerPage={25} />);

    // Initial page
    expect(screen.getByText(/page 1 of 2/i)).toBeInTheDocument();

    // Next page
    fireEvent.click(screen.getByRole("button", { name: /next/i }));
    expect(screen.getByText(/page 2 of 2/i)).toBeInTheDocument();

    // Last page shows remaining users (5 users)
    const cards = screen.getAllByTestId("user-card");
    expect(cards.length).toBe(5);

    // Previous page
    fireEvent.click(screen.getByRole("button", { name: /previous/i }));
    expect(screen.getByText(/page 1 of 2/i)).toBeInTheDocument();
    expect(screen.getAllByTestId("user-card").length).toBe(25);
  });

  it("disables next button on last page and previous on first page", () => {
    render(<UserBoard users={mockUsers} loading={false} error={null} itemsPerPage={25} />);

    const nextButton = screen.getByRole("button", { name: /next/i });
    const prevButton = screen.getByRole("button", { name: /previous/i });

    // Initially, previous disabled, next enabled
    expect(prevButton).toBeDisabled();
    expect(nextButton).toBeEnabled();

    // Go to last page
    fireEvent.click(nextButton);
    expect(prevButton).toBeEnabled();
    expect(nextButton).toBeDisabled();
  });
});
