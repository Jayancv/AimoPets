import { render, screen, fireEvent } from "@testing-library/react";
import CountryDropdown from "./CountryDropdown";
import { describe, it, expect, vi } from "vitest";

describe("CountryDropdown Component", () => {
  it("renders all countries plus 'All Countries' option", () => {
    const onCountryChange = vi.fn();
    render(<CountryDropdown selectedCountry="" onCountryChange={onCountryChange} />);

    // Check default option
    expect(screen.getByRole("option", { name: /all countries/i })).toBeInTheDocument();

    // Check a few country options
    expect(screen.getByRole("option", { name: /US - United States/i })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: /FR - France/i })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: /IN - India/i })).toBeInTheDocument();

    // Total options: countries.length + 1
    const options = screen.getAllByRole("option");
    expect(options.length).toBe(22); // 21 countries + All Countries
  });

  it("renders with selected country", () => {
    const onCountryChange = vi.fn();
    render(<CountryDropdown selectedCountry="US" onCountryChange={onCountryChange} />);

    const select = screen.getByRole("combobox") as HTMLSelectElement;
    expect(select.value).toBe("US");
  });

  it("calls onCountryChange when a country is selected", () => {
    const onCountryChange = vi.fn();
    render(<CountryDropdown selectedCountry="" onCountryChange={onCountryChange} />);

    const select = screen.getByRole("combobox");
    fireEvent.change(select, { target: { value: "IN" } });

    expect(onCountryChange).toHaveBeenCalledOnce();
    expect(onCountryChange).toHaveBeenCalledWith("IN");
  });
});
