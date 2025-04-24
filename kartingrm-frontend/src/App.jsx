import React, { Suspense, lazy } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import CssBaseline from '@mui/material/CssBaseline'
import Container from '@mui/material/Container'
import { CircularProgress } from '@mui/material'
import Navbar from './components/Navbar'

const Home             = lazy(() => import('./pages/Home'))
const WeeklyRack       = lazy(() => import('./pages/WeeklyRack'))
const ReservationForm  = lazy(() => import('./pages/ReservationForm'))
const ReservationsList = lazy(() => import('./pages/ReservationsList'))
const ReportCharts     = lazy(() => import('./pages/ReportCharts'))
const ClientsCrud      = lazy(() => import('./pages/ClientsCrud'))
const SessionsCrud     = lazy(() => import('./pages/SessionsCrud'))
const PaymentPage      = lazy(() => import('./pages/PaymentPage'))
const NotFound         = lazy(() => import('./pages/NotFound'))

export default function App() {
  return (
    <>
      <CssBaseline />
      <Navbar />
      <Container maxWidth="xl" sx={{ my: 4 }}>
        <Suspense fallback={
          <CircularProgress sx={{ display:'block', margin:'2rem auto' }}/>
        }>
          <Routes>
            <Route path="/" element={<Navigate to="/home" replace />} />
            <Route path="/home" element={<Home />} />
            <Route path="/rack" element={<WeeklyRack />} />
            <Route path="/reservations/new" element={<ReservationForm />} />
            <Route path="/reservations" element={<ReservationsList />} />
            <Route path="/payments/:reservationId" element={<PaymentPage />} />
            <Route path="/clients" element={<ClientsCrud />} />
            <Route path="/sessions" element={<SessionsCrud />} />
            <Route path="/reports" element={<ReportCharts />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </Container>
    </>
  )
}
