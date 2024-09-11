`timescale  1ns/1ns
module tb;
    // Testbench signals
    reg clk;
    reg rst_n;
    reg wr_en;
    reg rd_en;
    reg [7:0] buf_in;
    wire [7:0] buf_out;
    wire buf_full;
    wire buf_empty;

    // Instantiate the FIFO module
    parameter WIDTH = 8;
    parameter DEPTH = 8;

    sync_fifo #(WIDTH, DEPTH) uut (
        .clk(clk),
        .rst_n(rst_n),
        .wr_en(wr_en),
        .rd_en(rd_en),
        .data_in(buf_in),
        .data_out(buf_out),
        .full(buf_full),
        .empty(buf_empty)
    );

    // Clock generation
    initial begin
        clk = 0;
        forever #5 clk = ~clk;
    end

     // Reset generation
    initial begin
        rst_n = 1;
        #10 rst_n = 0;
        #10 rst_n = 1;
    end

    // Task definitions
    task push (input [WIDTH-1:0] data);
        if (buf_full)
            $display("---Cannot push %x: Buffer Full---",data);
        else begin
            $display("Push:%x",data);
            buf_in = data;
            wr_en = 1;
            @(posedge clk);
            #5 wr_en = 0;
        end
    endtask

    task pop ();
        if (buf_empty)
            $display("---Cannot pop: Buffer Empty---");
        else begin
            rd_en = 1;
            @(posedge clk);
            #3 rd_en = 0;
            $display("Poped:%x",buf_out);
        end
    endtask

    // Test sequence
    initial begin
        // Wait for reset deassertion
        wr_en <= 0;
        rd_en <= 0;
        @(negedge rst_n);
        @(posedge rst_n);

        // Push and pop operations
        push(8'h1A);
        push(8'h35);
        push(8'h4F);
        push(8'ha1);
        push(8'h23);
        push(8'h37);
        push(8'h99);
        push(8'h0B);
        push(8'h72);

        #20;

        pop();
        pop();
        pop();
        pop();
        pop();
        pop();
        pop();
        pop();
        pop();

        #20;

        push(8'h1A);
        pop();
        push(8'h35);
        pop();

        $finish;
  end

    initial begin
        $dumpfile("sync_fifo.vcd");
        $dumpvars(0,tb);
    end

endmodule