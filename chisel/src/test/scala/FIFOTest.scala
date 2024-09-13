package fifo

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class FIFOTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "SyncFIFO" 
  it should ("run") in {
    println("========== Test SyncFIFO ==========")
    test(new SyncFIFO(UInt(8.W), 8)).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      println("========== in test ==========")
      def push(data: Int) = {
          if(c.io.full.peek().litToBoolean){
              println(s"---Cannot push ${data}: Buffer Full---")
          }
          else {
              println(s"---Push ${data}---")
              c.io.data_in.poke(data.U)
              c.io.wr_en.poke(true.B)
              c.clock.step()
              c.io.wr_en.poke(false.B)
          }
      }
      def pop() = {
            if(c.io.empty.peek().litToBoolean){
                println("---Cannot pop: Buffer Empty---")
            }
            else {
                c.io.rd_en.poke(true.B)
                println(s"---Pop ${c.io.data_out.peek().litValue}---")
                c.clock.step()
                c.io.rd_en.poke(false.B)
            }
      }

      push (0x1A)
      push (0x35)
      push (0x4F)
      push (0xa1)
      push (0x23)
      push (0x37)
      push (0x99)
      push (0x0B)
      push (0x72)

      for(i <- 0 until 9){
          pop()
      }
    }
    println("========== end test ==========")
  }
}