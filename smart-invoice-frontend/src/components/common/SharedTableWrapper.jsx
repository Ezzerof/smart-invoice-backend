import React from "react";

export default function SharedTableWrapper({ children }) {
  return (
    <div className="w-full overflow-x-auto shadow-lg rounded-xl border border-zinc-700 bg-[#242424]">
      <table className="w-full min-w-[800px]">{children}</table>
    </div>
  );
}