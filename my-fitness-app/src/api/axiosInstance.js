import axios from "axios";

// const instance = axios.create({
//   baseURL: "http://localhost:8080",
// });
const instance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

// 每次請求都自動加上 token
instance.interceptors.request.use(
  (config) => {
    console.log("API base URL: ", import.meta.env.VITE_API_URL);
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
