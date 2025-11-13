
interface CountryDropDownProps {
  selectedCountry: string;
  onCountryChange: (country: string) => void;
}

// Map of country codes to full names
const countries: { code: string; name: string }[] = [
  { code: "AU", name: "Australia" },
  { code: "BR", name: "Brazil" },
  { code: "CA", name: "Canada" },
  { code: "CH", name: "Switzerland" },
  { code: "DE", name: "Germany" },
  { code: "DK", name: "Denmark" },
  { code: "ES", name: "Spain" },
  { code: "FI", name: "Finland" },
  { code: "FR", name: "France" },
  { code: "GB", name: "United Kingdom" },
  { code: "IE", name: "Ireland" },
  { code: "IN", name: "India" },
  { code: "IR", name: "Iran" },
  { code: "MX", name: "Mexico" },
  { code: "NL", name: "Netherlands" },
  { code: "NO", name: "Norway" },
  { code: "NZ", name: "New Zealand" },
  { code: "RS", name: "Serbia" },
  { code: "TR", name: "Turkey" },
  { code: "UA", name: "Ukraine" },
  { code: "US", name: "United States" },
];

const CountryDropdown= ({ selectedCountry, onCountryChange }: CountryDropDownProps) => {
   return (
    <select
      name="country"
      value={selectedCountry}
      onChange={(e) => onCountryChange(e.target.value)}
      className="border border-gray-300 rounded-lg px-3 py-2 w-56"
    >
      <option value="">All Countries</option>
      {countries.map((country) => (
        <option key={country.code} value={country.code}>
          {country.code} - {country.name}
        </option>
      ))}
    </select>
  );
};

export default CountryDropdown;
