import {
  Badge,
  Card,
  Flex,
  Group,
  Image,
  Text,
  useMantineTheme,
} from '@mantine/core'
import { IconLoader2 } from '@tabler/icons-react'
import { ethos } from 'ethos-connect'
import { ref, set } from 'firebase/database'
import { useEffect } from 'react'
import { db } from '../../config/firebase'
import { MainLayout } from '../../layout/MainLayout'

export default function MyNFTPage() {
  const { wallet } = ethos.useWallet()
  const theme = useMantineTheme()

  useEffect(() => {
    if (wallet?.contents?.nfts && wallet?.address) {
      set(ref(db, 'users/' + wallet?.address.toString()), {
        nfts: wallet?.contents?.nfts.map((items) => ({
          name: items.name,
          description: items.description,
          imageUrl: items.imageUrl,
          chain: items.chain,
        })),
      })
    }
  }, [wallet])

  return (
    <MainLayout>
      {wallet?.address ? (
        <Flex gap={10} wrap={'wrap'}>
          {wallet?.contents?.nfts.map((items) => (
            <Card
              shadow="sm"
              padding="lg"
              radius="sm"
              bg={theme.colors.dark[8]}
            >
              <Card.Section>
                <Image src={items.imageUrl!} height={200} alt="Norway" />
              </Card.Section>

              <Group justify="space-between" mt="md" mb="xs">
                <Text fw={500}>{items.name}</Text>
                <Badge color="pink" variant="light">
                  {items.chain}
                </Badge>
              </Group>

              <Text size="sm" c="dimmed">
                {items.description}
              </Text>
            </Card>
          ))}
        </Flex>
      ) : (
        <IconLoader2 size={32} color="white" />
      )}
    </MainLayout>
  )
}
