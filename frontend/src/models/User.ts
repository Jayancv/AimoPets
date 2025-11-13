export interface Dob {
  date: string;
  age: number;
}

export interface User {
  id: string;
  gender: string;
  country: string;
  name: string;
  email: string;
  dob: Dob;
  phone: string;
  petImage: string;
}
