package info.p2sh.bitcoink.datastructures

import info.p2sh.bitcoink.utils.fromHex
import org.junit.Assert
import org.junit.Test

class TransactionTest {
    @Test
    fun correctlyParsesLegacyTx() {
        val txHex = TransactionTest::class.java.getResourceAsStream("/tx.dat").use {
            it.readBytes(224)
        }

        val parsed = Transaction.fromHex(txHex)

        Assert.assertEquals("0ea2c4d4b34c4ef11bfc4a79e26b1aefd8258f5f1292587d792018d63d94d4b5", parsed.txid.toString())
        Assert.assertArrayEquals(txHex, parsed.bytes)

        Assert.assertArrayEquals(parsed.serialize(stripped = false), parsed.serialize(stripped = true))

        Assert.assertFalse(parsed.isCoinbase)
        Assert.assertEquals(1, parsed.inputs.size)
        Assert.assertEquals("80e48c69f4ef495ee8208083002dbaa1c93d4cc64f880b7bb6764e400abec964", parsed.inputs[0].transaction.toString())
        Assert.assertEquals(0, parsed.inputs[0].index)
        Assert.assertTrue(parsed.inputs[0].witness.isEmpty())
        Assert.assertArrayEquals("483045022100e68dd660d4495333fc8fc6f86e4ffef4afaa2e668ccd4e4df4819cc2174edacf0220182c2e3e02ab2c7d94f081b1ae0eea650800b011bec4429d188a268989192dbe0121026af140a982f8dc3be2981db575ae8aa55f628fd97424879964120ac92fb442cd".fromHex(), parsed.inputs[0].script)
        Assert.assertEquals(4294967295, parsed.inputs[0].sequenceNumber)
        Assert.assertEquals(2, parsed.outputs.size)
        Assert.assertEquals(200000000, parsed.outputs[0].value)
        Assert.assertArrayEquals("a914d99f183fef3a1598cc31ac8bce206ac4cf9e301f87".fromHex(), parsed.outputs[0].script)
        Assert.assertEquals(899864371, parsed.outputs[1].value)
        Assert.assertArrayEquals("76a9146340cd09b2a974bc1eab5be7263df34ec1cac62388ac".fromHex(), parsed.outputs[1].script)
        Assert.assertEquals(1, parsed.version)
        Assert.assertEquals(224, parsed.bytes.size)
        Assert.assertEquals(896, parsed.weight)
    }

    @Test
    fun correctlyParsesSegWitTx() {
        val txHex = TransactionTest::class.java.getResourceAsStream("/segwit_tx.dat").use {
            it.readBytes(314)
        }

        val parsed = Transaction.fromHex(txHex)

        Assert.assertEquals("7e4953fd38072cce7ca2b87d120999130ea10d2dfdb338da68f712a2593c8a6d", parsed.txid.toString())
        Assert.assertArrayEquals(txHex, parsed.serialize(stripped = false))

        Assert.assertFalse(parsed.isCoinbase)
        Assert.assertEquals(1, parsed.inputs.size)
        Assert.assertEquals("3f03a6bc4023fa086bcc9e2fc77c238f9116950cf0b9b6cbff7065e45bcb1176", parsed.inputs[0].transaction.toString())
        Assert.assertEquals(0, parsed.inputs[0].index)
        // Pure segwit has no input script
        Assert.assertEquals(0, parsed.inputs[0].script.size)
        Assert.assertEquals(4, parsed.inputs[0].witness.size)
        Assert.assertArrayEquals(ByteArray(0), parsed.inputs[0].witness[0])
        Assert.assertArrayEquals("304402201430e0688d79a958ce7c3b5d8daa4ad8bb27793c72a357cd1ea1520bb584d0bc022049bea10fe7ef4aaa0b22a0e66421a1b08b9a0a6dd8bceb00f1f1009cf198195601".fromHex(), parsed.inputs[0].witness[1])
        Assert.assertArrayEquals("304402200a3fcc98b3f8671ca0df946c123b76e35bed52f3efde3224b92a8d15e871d7a702200dfe0867bbdfa97cc1a9c4ee56be1c919c70e624fdd9fa92dd27f6704d51af1401".fromHex(), parsed.inputs[0].witness[2])
        Assert.assertArrayEquals("52210399b900c50e951a5daea94f941e4d2b44a6e5583ff767e6871c0213e640d507832103dab6677b3001008f8ccd3a204ffebb21b1c40cbfd5c87e0e255a2222be51259752ae".fromHex(), parsed.inputs[0].witness[3])
        Assert.assertEquals(2157791544, parsed.inputs[0].sequenceNumber)
        Assert.assertEquals(1, parsed.outputs.size)
        Assert.assertEquals(48274, parsed.outputs[0].value)
        Assert.assertArrayEquals("002059778b96d8d7c8c00b55eae8560df6660069e8fe0068ab2913a575942fcad719".fromHex(), parsed.outputs[0].script)
        Assert.assertEquals(2, parsed.version)
        Assert.assertEquals(94, parsed.bytes.size)
        Assert.assertEquals(314, parsed.serialize(stripped = false).size)
        Assert.assertEquals(596, parsed.weight)
    }
}