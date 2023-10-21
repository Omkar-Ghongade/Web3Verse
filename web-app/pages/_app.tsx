import '@mantine/core/styles.css'
import { Chain, EthosConnectProvider } from 'ethos-connect'
import { Inter } from 'next/font/google'
import ExampleIcon from '../icons/ExampleIcon'
import '../styles/globals.css'

import { MantineProvider } from '@mantine/core'
import type { AppProps } from 'next/app'
import Head from 'next/head'
import AuthProvider from '../hooks/authProvider'
import { NETWORK } from '../lib/constants'

const inter = Inter({
  variable: '--font-inter',
  subsets: ['latin'],
})

function MyApp({ Component, pageProps }: AppProps) {
  const ethosConfiguration = {
    apiKey: process.env.NEXT_PUBLIC_ETHOS_API_KEY,
    preferredWallets: ['Ethos Wallet'],
    network: NETWORK,
    chain: Chain.SUI_TESTNET,
  }

  return (
    <AuthProvider>
      <MantineProvider
        defaultColorScheme="dark"
        theme={{
          fontFamily: inter.style.fontFamily,
          primaryColor: 'pink',
        }}
      >
        <EthosConnectProvider
          ethosConfiguration={ethosConfiguration}
          dappName="Unfold Project"
          dappIcon={<ExampleIcon />}
          connectMessage="Connect to Unfold"
        >
          <Head>
            <title>Unfold</title>
          </Head>
          <Component {...pageProps} />
        </EthosConnectProvider>
      </MantineProvider>
    </AuthProvider>
  )
}

export default MyApp
