import React, { useState } from "react";
import CountryDropdown from "./CountryDropdown";
import "./Filter.css";

interface FilterProps {
  onFilter: (count: number, nat: string) => void;
  loading: boolean;
}

const UserFilter = ({ onFilter, loading }: FilterProps) => {
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
        max={20000}
      />
      {/* Country dropdown */}
      <CountryDropdown selectedCountry={nat} onCountryChange={setNat} />

      <button name="fetch" type="submit"  disabled={loading} className="user-filter-button">
        Fetch Data
      </button>
    </form>
  );
};

export default React.memo(UserFilter);
