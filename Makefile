SRC = sync_fifo.v
TESTBENCH = tb.v
VVP = sync_fifo.vvp
VCD = sync_fifo.vcd

sim: $(SRC) $(TESTBENCH)
	iverilog -o $(VVP) $^

run: sim
	vvp $(VVP)

gtkwave: $(VCD)
	gtkwave $(VCD) &