package info.p2sh.bitcoink.datastructures

import org.junit.Assert
import org.junit.Test
import javax.xml.bind.DatatypeConverter

class BlockTest {
    @Test
    fun correctlyParsesGenesisBlock() {
        val genesisHex = BlockTest::class.java.getResourceAsStream("/genesis.dat").use {
            it.readBytes(285)
        }

        val parsed = Block.fromHex(genesisHex)

        Assert.assertEquals(1, parsed.header.version)
        Assert.assertEquals(Sha256Hash(ByteArray(32)), parsed.header.previousBlock)
        Assert.assertEquals(1, parsed.transactions.size)

        val genesisTx = parsed.transactions[0]
        Assert.assertEquals(1, genesisTx.inputs.size)
        Assert.assertEquals(1, genesisTx.outputs.size)

        val genesString = DatatypeConverter.printHexBinary(genesisHex).toLowerCase()

        Assert.assertEquals(285, parsed.size)

        val bytesString = DatatypeConverter.printHexBinary(parsed.bytes).toLowerCase()
        Assert.assertEquals(genesString, bytesString)

        val genesisHash = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"
        Assert.assertEquals(genesisHash, parsed.hash.toString())

        val genesisTxHash = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"
        Assert.assertEquals(genesisTxHash, parsed.transactions[0].txid.toString())

        Assert.assertEquals("00".repeat(32), parsed.transactions[0].inputs[0].transaction.toString())

        Assert.assertArrayEquals(genesisHex, parsed.bytes)
    }
}