import axios from "axios";

export const fetchUsersWithPet = (count: number, nat?: string) => {
  return axios.get("/api/users-with-pet", {
    params: { results: count, nat }
  }).then(res => res.data);
};