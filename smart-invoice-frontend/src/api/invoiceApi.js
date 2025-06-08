const deleteInvoice = async (id) => {
  await fetch(`http://localhost:8080/api/invoices/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  });
};

const markAsPaid = async (id) => {
  await fetch(`http://localhost:8080/api/invoices/${id}/mark-paid`, {
    method: 'PATCH',
    credentials: 'include',
  });
};

const sendInvoiceEmail = async (id) => {
  await fetch(`http://localhost:8080/api/invoices/${id}/email`, {
    method: 'POST',
    credentials: 'include',
  });
};
