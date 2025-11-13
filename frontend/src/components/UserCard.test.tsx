import { render, screen } from "@testing-library/react";
import UserCard from "./UserCard";
import type { User } from "../models/User";
import { test, expect, describe } from 'vitest'
import '@testing-library/jest-dom/vitest'

// Mock user data
const mockUser: User = {
  id: "123",
  gender: "female",
  country: "FI",
  name: "Anna Han",
  email: "anna@example.com",
  dob: { date: "1990-01-01T00:00:00.000Z", age: 33 },
  phone: "123-456-7890",
  petImage: "https://images.dog.ceo/1.jpg",
};

describe("UserCard", () => {
  test("renders user information correctly", () => {
   const { container } = render(<UserCard user={mockUser} />);

    const details = container.querySelector('.user-card-info')
    // Name
    expect(details).toHaveTextContent('Anna Han')

    expect(details).toHaveTextContent('FEMALE • FI')
    expect(details).toHaveTextContent('anna@example.com')
    expect(details).toHaveTextContent('123-456-7890')
    expect(details).toHaveTextContent('1/1/1990')

    const img = screen.getByAltText("pet") as HTMLImageElement;
    expect(img).toBeInTheDocument();
    expect(img.src).toBe(mockUser.petImage);

  });
});
