import React, { useEffect, useState } from "react";
import ProductFormModal from "../components/products/ProductFormModal";
import ProductEditModal from "../components/products/ProductEditModal";
import SharedTableWrapper from "../components/common/SharedTableWrapper";
import { useAuth } from '../contexts/AuthContext';
import {
  createProduct,
  updateProduct,
  deleteProduct,
  fetchProducts,
} from "../api/productApi";

export default function Products() {
  const { user } = useAuth();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [isCreateOpen, setCreateOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);

  const [keyword, setKeyword] = useState("");
  const [sortBy, setSortBy] = useState("");


  const loadProducts = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (keyword) params.append("keyword", keyword);
      if (sortBy) params.append("sortBy", sortBy);

      const data = await fetch(`/api/products/filter?${params.toString()}`).then(res => res.json());
      setProducts(data);
    } catch (err) {
      console.error("Fetch error:", err);
      setError("Failed to fetch products.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!user) return;
    loadProducts();
  }, []);

  useEffect(() => {
    if (!user) return;
    if (sortBy) loadProducts();
  }, [sortBy]);

  const handleDelete = async (product) => {
    if (!window.confirm(`Delete ${product.name}?`)) return;
    try {
      await deleteProduct(product.id);
      setProducts((cur) => cur.filter((p) => p.id !== product.id));
    } catch (e) {
      alert("Failed to delete product");
    }
  };

  const handleAdd = (product) => {
    setProducts((cur) => [...cur, product]);
  };

  const handleUpdate = (updated) => {
    setProducts((cur) =>
      cur.map((p) => (p.id === updated.id ? updated : p))
    );
  };

  return (
    <section className="space-y-8">
      <header className="flex items-center justify-between py-4">
        <h1 className="text-3xl font-bold tracking-tight text-white">Products</h1>
        <button
          onClick={() => setCreateOpen(true)}
          className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-500 text-white"
        >
          + Add Product
        </button>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Search input and button side-by-side */}
        <div className="flex flex-col">
          <label className="mb-1 text-sm font-medium text-white">Search</label>
          <div className="flex gap-2">
            <input
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Search by name"
              className="flex-1 px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
            />
            <button
              onClick={loadProducts}
              className="px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-500 text-white"
            >
              Search
            </button>
          </div>
        </div>

        {/* Sort dropdown */}
        <div className="flex flex-col">
          <label className="mb-1 text-sm font-medium text-white">Sort By</label>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2 rounded border border-zinc-600 bg-zinc-800 text-white"
          >
            <option value="">Default</option>
            <option value="name">Name (A-Z)</option>
            <option value="-name">Name (Z-A)</option>
            <option value="price">Price (Low to High)</option>
            <option value="-price">Price (High to Low)</option>
          </select>
        </div>
      </div>



      {loading && <p className="text-zinc-400">Loading...</p>}
      {error && <p className="text-red-400">{error}</p>}

      {!loading && !error && products.length === 0 && (
        <p className="text-zinc-400">No products found.</p>
      )}

      {!loading && !error && products.length > 0 && (
        <SharedTableWrapper>
          <thead>
            <tr className="border-b border-zinc-700">
              {["Name", "Description", "Price", "Quantity", "Actions"].map((h) => (
                <th
                  key={h}
                  className={`px-6 py-3 text-center text-sm font-semibold text-zinc-300 ${
                    h === "Actions" ? "w-[100px]" : ""
                  }`}
                >
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr
                key={p.id}
                className="border-b border-zinc-700 hover:bg-zinc-800/40 transition-colors"
              >
                <td className="px-6 py-4 text-center text-white font-medium">
                  {p.name}
                </td>
                <td className="px-6 py-4 text-center text-zinc-300 min-w-[200px]">
                  {p.description}
                </td>
                <td className="px-6 py-4 text-center text-zinc-300">
                  £{p.price.toFixed(2)}
                </td>
                <td className="px-6 py-4 text-center text-zinc-300">
                  {p.quantity}
                </td>
                <td className="px-6 py-4">
                  <div className="flex justify-end gap-4 pr-4">
                    <button
                      onClick={() => setEditTarget(p)}
                      className="px-4 py-1.5 text-xs rounded-md bg-indigo-600 hover:bg-indigo-500 text-white transition-colors"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(p)}
                      className="px-4 py-1.5 text-xs rounded-md bg-rose-600 hover:bg-rose-500 text-white transition-colors"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </SharedTableWrapper>
      )}


      {/* Modals */}
      <ProductFormModal
        isOpen={isCreateOpen}
        onClose={() => setCreateOpen(false)}
        onProductCreated={handleAdd}
      />

      <ProductEditModal
        isOpen={!!editTarget}
        product={editTarget}
        onClose={() => setEditTarget(null)}
        onUpdated={handleUpdate}
      />
    </section>
  );
}
