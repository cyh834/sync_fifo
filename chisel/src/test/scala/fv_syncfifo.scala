package fifo

import chisel3._
import chisel3.util._
import chiseltest._
import chiseltest.formal._
import org.scalatest.flatspec.AnyFlatSpec

class FV_SyncFIFO[T <: Data](gen: T, depth: Int) extends SyncFIFO(gen, depth){
  // Assumptions
  // 1) Assume write enable is not active when reset = 1
  when(reset.asBool){
    assume(!io.wr_en, "write_en_off_rst_on")
  }

  // 2) Assume read enable is not active when reset = 1
  when(reset.asBool){
    assume(!io.rd_en, "read_en_off_rst_on")
  }

  // 3) Assume read enable is not active when empty is active
  when(io.empty){
    assume(!io.rd_en, "read_en_off_empty")
  }

  // 4) Assume write enable is not active when full is active
  when(io.full){
    assume(!io.wr_en, "write_en_off_full")
  }

  // Assertions
  // 1) The property assures that if FIFO is full then write enable signal must not be active.
  when(!reset.asBool && io.full) {
    assert(!io.wr_en, "The write enable signal is active when the FIFO is not full")
  }

  // 2) The property assures that if FIFO is empty then read enable signal must not be active.
  when(!reset.asBool && io.empty) {
    assert(!io.rd_en, "The read enable signal is active when the FIFO is not empty")
  }

  // 3) The property assures that write pointer increments when a write operation happen
  when(past(!reset.asBool && io.wr_en && !io.full)) {
    assert(past(wr_ptr.value) + 1.U === wr_ptr.value, "The write pointer increment when a write operation happen.")
  }

  // 4) The property assures that wr_ptr is stable if a write doesn't occur.
  when(past(!reset.asBool && !io.wr_en)) {
    assert(stable(wr_ptr.value), "The write pointer is stable.")
  }

  // 5) The property assures that rd_ptr is stable if a read doesn't occur.
  when(past(!reset.asBool && !io.rd_en)) {
    assert(stable(rd_ptr.value), "The read pointer is stable.")
  }

  // 6) After reset the read and write pointers must have the same value and be 0
  when(past(rose(reset.asBool))) {
    assert(rd_ptr.value === 0.U && wr_ptr.value === 0.U, "After reset the read and write pointers have the same value and be 0")
  }

  // 7) The full and empty flags can never be active at the same time 
  when(!reset.asBool) {
    assert(!(io.full && io.empty), "Full and Empty are not active at the same time")
  }

  // 8) When wr_ptr reaches max value wraps around to 0    
  when(past(!reset.asBool && io.wr_en && !io.full && (wr_ptr.value === (depth - 1).U))) {
    assert(wr_ptr.value === 0.U, "The write pointer return to zero")
  }

  // 9) When rd_ptr reaches max value wraps around to 0    
  when(past(!reset.asBool && io.rd_en && !io.empty && (rd_ptr.value === (depth - 1).U))) {
    assert(rd_ptr.value === 0.U, "The read pointer return to zero")
  }

  // 10) Empty signal active after reset
  when(past(rose(reset.asBool))) {
    assert(io.empty, "Empty signal active when reset")
  }

  // 11) Full signal off after reset
  when(past(rose(reset.asBool))) {
    assert(!io.full, "Full signal off when reset")
  }

  // 12) This property verifies write_data was written correctly when the write_en is activated
  when(past(!reset.asBool && io.wr_en && !io.full)) {
    assert(memReg(past(wr_ptr.value)) === past(io.data_in), "write_data was written correctly when the write_en is activated")
  }

  // 13) This property verifies read_en was read correctly when read_data is activated
  when(past(!reset.asBool && io.rd_en && !io.empty)) {
    assert(memReg(past(rd_ptr.value)) === past(io.data_out), "read_data was read correctly when the read_en is activated")
  }

  // 14) The property assures that FIFO memory value is stable if write_en is not active
  when(past(!reset.asBool && !io.wr_en)) {
    assert(stable(memReg(wr_ptr.value)), "FIFO memory value is stable when write_en is not active")
  }
  
  //Cover is not yet implemented for Chiseltest
  /*
  // Cover properties
  // 1) Cover that the FIFO becomes full
  cover(io.full, "fifo_full")

  // 2) Cover that the FIFO becomes empty
  cover(io.empty, "fifo_empty")

  // 3) Cover that the write enable signal is asserted when the FIFO is not full
  cover(io.wr_en && !io.full, "fifo_not_full")

  // 4) Cover that the read enable signal is asserted when the FIFO is not empty
  cover(io.rd_en && !io.empty, "fifo_not_empty")

  // 5) All the memory was written
  for(i <- 0 until depth){
    cover(io.wr_en && (wr_ptr.value === i.U), "write_all_address")
  }

  // 6) All the memory was read
  for(i <- 0 until depth){
    cover(io.rd_en && (rd_ptr.value === i.U), "read_all_address")
  }

  // 7) Cover what could happen if the write enable signal is asserted when the FIFO is full
  cover(io.wr_en && io.full, "write_en_fifo_full")

  // 8) Cover what could happen if the read enable signal is asserted when the FIFO is empty
  cover(io.rd_en && io.empty, "read_en_fifo_empty")

  // 9) Read and write at the same time.
  cover(io.wr_en && io.rd_en, "write_and_read")

  // 10) Read and write at the same time while the memory is full.
  cover(io.full && io.rd_en && io.wr_en, "write_and_read_mem_full")

  // 11) Read and write at the same time while the memory is empty.
  cover(io.empty && io.rd_en && io.wr_en, "write_and_read_mem_empty")

  // 12) Cover a sequence when FIFO becomes full and then no full
  cover(fell(io.full), "fifo_full_no_full")

  // 13) Cover a sequence when FIFO becomes empty and then no empty
  cover(fell(io.empty), "fifo_empty_no_empty")
  */
}

class FIFOFormalTest extends AnyFlatSpec with ChiselScalatestTester with Formal {
  "Test" should "pass" in {
    verify(new FV_SyncFIFO(UInt(8.W), 8), Seq(BoundedCheck(40),BtormcEngineAnnotation))
  }
}
