import React, { Suspense, lazy } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import CssBaseline from '@mui/material/CssBaseline'
import Container from '@mui/material/Container'
import { CircularProgress } from '@mui/material'
import Navbar from './components/Navbar'

// Lazy-load de cada página
const Home             = lazy(() => import('./pages/Home'))
const WeeklyRack       = lazy(() => import('./pages/WeeklyRack'))
const ReservationForm  = lazy(() => import('./pages/ReservationForm'))
const ReservationsList = lazy(() => import('./pages/ReservationsList'))
const ReportCharts     = lazy(() => import('./pages/ReportCharts'))
const NotFound         = lazy(() => import('./pages/NotFound'))

export default function App() {
  return (
    <>
      <CssBaseline />
      <Navbar />
      <Container sx={{ mt: 4 }}>
        <Suspense
          fallback={
            <CircularProgress
              sx={{ display: 'block', margin: '2rem auto' }}
            />
          }
        >
          <Routes>
            <Route path="/" element={<Navigate to="/home" replace />} />
            <Route path="/home" element={<Home />} />
            <Route path="/rack" element={<WeeklyRack />} />
            <Route path="/reservations/new" element={<ReservationForm />} />
            <Route path="/reservations" element={<ReservationsList />} />
            <Route path="/reports" element={<ReportCharts />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </Container>
    </>
  )
}
