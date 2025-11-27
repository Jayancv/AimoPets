import axios from "axios";

export const fetchUsersWithPet = (count: number, nat?: string, signal?: AbortSignal) => {
  return axios.get("/api/users-with-pet", {
    params: { results: count, nat },
    signal
  }).then(res => res.data)
    .catch((err) => {
      // Abort → rethrow so React can ignore it
      if (err instanceof DOMException && err.name === "AbortError") {
        throw err;
      }

      // Axios backend error message
      if (axios.isAxiosError(err) && err.response?.data?.error) {
        throw new Error(err.response.data.error);
      }

      // Fallback generic error
      throw new Error("Failed to fetch users.");
    });
};