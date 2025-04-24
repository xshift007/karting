import React from 'react'
import { Container, Typography, Button } from '@mui/material'

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError() {
    return { hasError: true }
  }

  componentDidCatch(error, info) {
    // puedes integrarlo con un servicio de logging externo
    console.error('Error capturado por ErrorBoundary:', error, info)
  }

  handleReload = () => {
    this.setState({ hasError: false })
    window.location.reload()
  }

  render() {
    if (this.state.hasError) {
      return (
        <Container sx={{ mt: 4, textAlign: 'center' }}>
          <Typography variant="h4" gutterBottom>
            ¡Uy! Algo salió mal.
          </Typography>
          <Button variant="contained" onClick={this.handleReload}>
            Recargar
          </Button>
        </Container>
      )
    }

    return this.props.children
  }
}
