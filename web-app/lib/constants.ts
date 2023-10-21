export const NETWORK: 'devnet' | 'testnet' | undefined = (process.env.NETWORK ||
  process.env.NEXT_PUBLIC_NETWORK) as 'devnet' | 'testnet' | undefined
export const FAUCET = process.env.FAUCET || process.env.NEXT_PUBLIC_FAUCET
export const ETHOS_EXAMPLE_CONTRACT = process.env.NEXT_PUBLIC_ETHOS_EXAMPLE_CONTRACT
export const ETHOS_EXAMPLE_COIN_TREASURY_CAP = process.env.NEXT_PUBLIC_ETHOS_EXAMPLE_COIN_TREASURY_CAP
export const ETHOS_COIN_TYPE = `${ETHOS_EXAMPLE_CONTRACT}::ethos_example_coin::ETHOS_EXAMPLE_COIN`
