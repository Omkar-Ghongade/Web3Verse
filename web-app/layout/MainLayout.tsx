import { AppShell, Badge, Box, Image, useMantineTheme } from '@mantine/core'
import { NavbarLinks } from '../components/common/NavbarLink'

export const MainLayout = ({ children }: { children: React.ReactNode }) => {
  const theme = useMantineTheme()

  return (
    <AppShell
      header={{ height: 55 }}
      navbar={{ width: 275, breakpoint: 'none' }}
      bg={theme.colors.dark[7]}
    >
      <AppShell.Header bg={theme.colors.dark[7]}>
        <Box
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            width: '100%',
            height: '100%',
            padding: '0 20px',
          }}
        >
          <Box>
            <Image
              src={'/brand-image.jpeg'}
              width={50}
              height={50}
              radius="xl"
            />
          </Box>
          <Badge variant="outline" color="pink">
            Credit Score : 500
          </Badge>
        </Box>
      </AppShell.Header>
      <AppShell.Navbar p="md" bg={theme.colors.dark[7]}>
        <NavbarLinks />
      </AppShell.Navbar>
      <AppShell.Main>
        <Box p="md">{children}</Box>
      </AppShell.Main>
    </AppShell>
  )
}
