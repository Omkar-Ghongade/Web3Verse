import WormholeBridge from '@wormhole-foundation/wormhole-connect'
import { MainLayout } from '../../layout/MainLayout'

export default function CrossChainTransations() {
  return (
    <MainLayout>
      {/* <Divider label="OR" /> */}
      <WormholeBridge />
    </MainLayout>
  )
}
