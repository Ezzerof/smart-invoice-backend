const BASE_URL = "/api/products";

export async function fetchProducts() {
  const res = await fetch(BASE_URL);
  if (!res.ok) throw new Error("Failed to fetch products");
  return await res.json();
}

export async function createProduct(product) {
  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });
  if (!res.ok) throw new Error("Failed to create product");
  return await res.json();
}

export async function updateProduct(id, product) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });
  if (!res.ok) throw new Error("Failed to update product");
  return await res.json();
}

export async function deleteProduct(id) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw new Error("Failed to delete product");
}
