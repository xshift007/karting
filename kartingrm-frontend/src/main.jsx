// main.jsx
import { Suspense, lazy } from 'react';
const Home         = lazy(()=>import('./pages/Home'));
const WeeklyRack   = lazy(()=>import('./pages/WeeklyRack'));
…

<BrowserRouter>
  <Suspense fallback={<CircularProgress/>}>
    <Routes>
      <Route path="/" element={<Navigate to="/home" replace />}/>
      <Route element={<Layout/>}>        {/* contenedor con Navbar  */}
        <Route path="home"          element={<Home/>}/>
        <Route path="rack"          element={<WeeklyRack/>}/>
        …
      </Route>
      <Route path="*" element={<NotFound/>}/>
    </Routes>
  </Suspense>
</BrowserRouter>
