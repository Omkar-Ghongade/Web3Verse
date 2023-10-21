const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const { SpheronClient, ProtocolEnum } = require("@spheron/storage");

dotenv.config();

const app = express();
const PORT = process.env.PORT || 8111;
const SPHERON_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlLZXkiOiJjZWVhNzcwYzI0NjViNGMyM2Q3MmQ2ZmI5MTcyNDMzZGQzYTk2NjUxMDA1N2IwZGY4YmU5ODYxZWNjNjgwZGMxYjI5NTZjYjNhZDI0ODc5YmEzYjVlYzNiYjY5ZjdiZmM1NzUwNWY3MTExNDEzNmI5ZTIwM2I0YWEzMWMyMGY0NCIsImlhdCI6MTY5NzcyMTIxNiwiaXNzIjoid3d3LnNwaGVyb24ubmV0d29yayJ9.k9E0xYHDB51nEpp9_769FGMl18fwWWA5BgNvaKphOY0"

app.use(cors());

app.get("/initiate-upload", async (req, res, next) => {
  try {
    const bucketName = "ntf-bucket";
    const protocol = ProtocolEnum.IPFS;

    const client = new SpheronClient({
      token: SPHERON_TOKEN,
    });

    const { uploadToken } = await client.createSingleUploadToken({
      name: bucketName,
      protocol,
    });

    console.log(uploadToken);

    res.status(200).json({
      uploadToken,
    });
  } catch (error) {
    console.error(error);
    next(error);
  }
});

app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
