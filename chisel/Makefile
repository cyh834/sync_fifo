fifo-test:
	mill -i top.test.testOnly fifo.FIFOTest

fifo-wave:
	gtkwave test_run_dir/SyncFIFO_should_run/SyncFIFO.vcd &

sub-test:
	mill -i top.test.testOnly Sub.FormalTest

fifo-formal:
	mill -i top.test.testOnly fifo.FIFOFormalTest