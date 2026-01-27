import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import CustomerMenuPage from './pages/CustomerMenuPage';
import StaffOrdersPage from './pages/StaffOrdersPage';
import PrintReceiptPage from './pages/PrintReceiptPage';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/staff/print/:orderId" element={<PrintReceiptPage />} />
        <Route path="/" element={<StaffOrdersPage />} />
        <Route path="/customer" element={<CustomerMenuPage />} />
      </Routes>
    </Router>
  );
}
