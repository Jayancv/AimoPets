import { render, screen, fireEvent } from "@testing-library/react";
import UserFilter from "./Filter";
import { describe, it, expect, vi } from "vitest";

interface MockCountryDropdownProps {
  selectedCountry: string;
  onCountryChange: (country: string) => void;
}

// Mock CountryDropdown to simplify testing
vi.mock("./CountryDropdown", () => ({
  default: ({ selectedCountry, onCountryChange }: MockCountryDropdownProps) => (
    <select
      data-testid="country-dropdown"
      value={selectedCountry}
      onChange={(e) => onCountryChange(e.target.value)}
    >
      <option value="">All</option>
      <option value="US">US-USA</option>
      <option value="GB">GB-UK</option>
    </select>
  ),
}));

describe("Filter Component", () => {
  it("renders inputs and button", () => {
    render(<UserFilter onFilter={vi.fn()} />);
    expect(screen.getByPlaceholderText(/count/i)).toBeInTheDocument();
    expect(screen.getByTestId("country-dropdown")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /fetch data/i })).toBeInTheDocument();
  });

  it("updates count input", () => {
    render(<UserFilter onFilter={vi.fn()} />);
    const input = screen.getByPlaceholderText(/count/i) as HTMLInputElement;
    fireEvent.change(input, { target: { value: "10" } });
    expect(input.value).toBe("10");
  });

  it("updates country dropdown", () => {
    render(<UserFilter onFilter={vi.fn()} />);
    const dropdown = screen.getByTestId("country-dropdown") as HTMLSelectElement;
    fireEvent.change(dropdown, { target: { value: "US" } });
    expect(dropdown.value).toBe("US");
  });

  it("calls onFilter with correct values on submit", () => {
    const onFilterMock = vi.fn();
    render(<UserFilter onFilter={onFilterMock} />);

    const input = screen.getByPlaceholderText(/count/i) as HTMLInputElement;
    const dropdown = screen.getByTestId("country-dropdown") as HTMLSelectElement;
    const button = screen.getByRole("button", { name: /fetch data/i });

    fireEvent.change(input, { target: { value: "15" } });
    fireEvent.change(dropdown, { target: { value: "GB" } });
    fireEvent.click(button);

    expect(onFilterMock).toHaveBeenCalledOnce();
    expect(onFilterMock).toHaveBeenCalledWith(15, "GB");
  });
});
