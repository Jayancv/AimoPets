import React, { useState } from "react";
import CountryDropdown from "./CountryDropdown";

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
    <form
      onSubmit={handleSubmit}
      className="flex flex-col sm:flex-row gap-3 mb-6 items-center justify-center"
    >
      <input
        name="count"
        type="number"
        value={count}
        onChange={(e) => setCount(Number(e.target.value))}
        className="border border-gray-300 rounded-lg px-3 py-2 w-32"
        placeholder="Count"
        min={1}
        max={1000}
      />
      {/* Country dropdown */}
      <CountryDropdown selectedCountry={nat} onCountryChange={setNat} />

      <button
       name="fetch"
        type="submit"
        className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg"
      >
        Fetch Data
      </button>
    </form>
  );
};

export default UserFilter;
