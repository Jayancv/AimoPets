import React, { useState } from "react";
import CountryDropdown from "./CountryDropdown";
import "./Filter.css";

interface FilterProps {
  onFilter: (count: number, nat: string) => void;
}

const UserFilter = ({ onFilter }: FilterProps) => {
  const [count, setCount] = useState<number>(5);
  const [nat, setNat] = useState<string>("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onFilter(count, nat);
  };

  return (
    <form onSubmit={handleSubmit} className="user-filter-form">
      <input
        name="count"
        type="number"
        value={count}
        onChange={(e) => setCount(Number(e.target.value))}
        className="user-filter-input"
        placeholder="Count"
        min={1}
        max={2500}
      />
      {/* Country dropdown */}
      <CountryDropdown selectedCountry={nat} onCountryChange={setNat} />

      <button name="fetch" type="submit" className="user-filter-button">
        Fetch Data
      </button>
    </form>
  );
};

export default UserFilter;
