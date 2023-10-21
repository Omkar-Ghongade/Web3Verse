import { useRouter } from 'next/router'
import { useEffect } from 'react'
import useAuthContext from '../hooks/context/useAuthContext'

const AuthPage = () => {
  const { push } = useRouter()
  const { setJwt } = useAuthContext()

  useEffect(() => {
    const hash = window.location.hash
    const params = new URLSearchParams(hash.slice(1))
    const jwt = params.get('id_token')

    if (jwt) {
      setJwt(jwt)
      push('/')
    } else {
      push('/login')
    }
  }, [push, setJwt])

  return <div>Loading...</div>
}

export default AuthPage
