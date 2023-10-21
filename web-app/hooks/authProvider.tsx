import { Ed25519Keypair } from '@mysten/sui.js/dist/cjs/keypairs/ed25519'
import { jwtToAddress } from '@mysten/zklogin'
import { useRouter } from 'next/router'
import {
  createContext,
  Dispatch,
  ReactNode,
  SetStateAction,
  useCallback,
  useEffect,
  useState,
} from 'react'
import useLocalStorage from './useLocalStorage'

export interface MetaData {
  maxEpoch: string
  ephemeralKeyPair: Ed25519Keypair
  randomness: bigint
  nonce: string
}

export interface User {
  jwt: string
  salt: string
  address: string
}
export interface AuthContextValue {
  setJwt: Dispatch<SetStateAction<string>>
  user: User | null
  logout: () => void
  loading: boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

interface AuthProviderProps {
  children: ReactNode
}

const PROTECTED_ROUTES = ['']

const AuthProvider = ({ children }: AuthProviderProps) => {
  const { push, pathname } = useRouter()
  const [user, setUser] = useState<User | null>(null)
  const [jwt, setJwt] = useLocalStorage('jwt', '')
  const [loading, setLoading] = useState(false)

  const logout = useCallback(() => {
    setJwt('')
    setUser(null)
  }, [setJwt])

  const reAuthenticate = useCallback(async () => {
    if (!jwt) return
    try {
      setLoading(true)
      const response = await fetch(`/api/auth?jwt=${jwt}`)
      const result = await response.json()
      if (result && typeof result !== 'object') {
        throw new Error('Response is not in JSON format')
      }
      const salt = result.salt
      if (salt) {
        const address = jwtToAddress(jwt, salt)
        setUser({
          jwt,
          salt,
          address,
        })
      }
    } catch (error) {
      console.error(error)
      // if (error === "JWK has expired") logout()
    }
    setLoading(false)
  }, [jwt])

  useEffect(() => {
    if (!jwt && PROTECTED_ROUTES.includes(pathname)) push('/login')
  }, [jwt, pathname, push])

  useEffect(() => {
    reAuthenticate()
  }, [reAuthenticate])

  return (
    <AuthContext.Provider value={{ setJwt, user, logout, loading }}>
      {loading ? <div className="min-w-[20px]" /> : children}
    </AuthContext.Provider>
  )
}

export { AuthContext }
export default AuthProvider
