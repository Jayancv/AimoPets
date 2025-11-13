import { MdEmail, MdPhone, MdCake } from "react-icons/md";
import "./UserCard.css";

interface Dob {
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

interface UserCardProps {
  user: User;
}

const UserCard = ({ user }:UserCardProps) => {
  return (
    <div className="user-card">
      <div className="user-card-image">
        <img
          src={user.petImage}
          alt="pet"
          className="w-full h-full object-cover"
        />
      </div>
      <div className="user-card-info">
        <h2 className="user-card-name">{user.name}</h2>
        <p className="user-card-gender-country">
          {user.gender.toUpperCase()} • {user.country}
        </p>

           <div className="user-card-field">
          <MdEmail  className="user-card-icon" /> <span>{user.email}</span>
        </div>
        <div className="user-card-field">
          <MdPhone  className="user-card-icon" /> <span>{user.phone}</span>
        </div>
        <div className="flex items-center gap-2 text-xs text-gray-400">
          <MdCake  className="user-card-icon" />{" "}
          <span>
            {new Date(user.dob.date).toLocaleDateString()} ({user.dob.age}y)
          </span>
        </div>
        
      </div>
    </div>
  );
};

export default UserCard;
