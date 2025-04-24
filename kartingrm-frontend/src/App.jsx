import { Routes, Route, Navigate } from 'react-router-dom'
import CssBaseline from '@mui/material/CssBaseline'
import Container from '@mui/material/Container'
import Navbar from './components/Navbar'
import Home from './pages/Home'

import WeeklyRack from './pages/WeeklyRack'
import ReservationForm from './pages/ReservationForm'
import ReservationsList from './pages/ReservationsList'
import ReportCharts from './pages/ReportCharts'
import NotFound from './pages/NotFound'

export default function App() {
  return (
    <>
      <CssBaseline/>
      <Navbar/>
      <Container sx={{ mt: 4 }}>
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace/>}/>
          <Route path="/home" element={<Home/>}/>
          <Route path="/rack" element={<WeeklyRack/>}/>
          <Route path="/reservations/new" element={<ReservationForm/>}/>
          <Route path="/reservations" element={<ReservationsList/>}/>
          <Route path="/reports" element={<ReportCharts/>}/>
          <Route path="*" element={<NotFound/>}/>
        </Routes>
      </Container>
    </>
  )
}
