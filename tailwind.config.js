/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/index.html",
    "src/css/*.css",
    "src/ts/*.ts",
    "src/ts/**/*.ts",
  ],
  theme: {
    extend: {
      colors: {
        white: {
          1: "#f9f9f9",
          2: "#f1f1f1",
          3: "#cccaca",
        },
        dark: {
          celeria: "#131313",
          "celeria-2": "#303030",
        },
        green: {
          celeria: "#097b83",
        },
      },
      minWidth: {
        64: "16rem",
      },
      maxWidth: {
        80: "20rem",
      },
    },
  },
  plugins: [],
};
