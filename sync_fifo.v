module sync_fifo 
#(
  parameter WIDTH = 'd8, 
  parameter DEPTH = 'd8
)
(
  input                  clk,
  input                  rst_n,
  input                  wr_en,
  input                  rd_en,
  input      [WIDTH-1:0] data_in,
  output reg [WIDTH-1:0] data_out,

  output reg             full,
  output reg             empty
);
  reg     [$clog2(DEPTH)-1:0]   fifo_wr_ptr ;    //fifo write pointer
  reg     [$clog2(DEPTH)-1:0]   fifo_rd_ptr ;    //fifo write pointer
  reg     [WIDTH-1:0] fifo_mem [DEPTH-1:0] ;     //fifo memory

  wire    [$clog2(DEPTH)-1:0]   next_wr_ptr = fifo_wr_ptr + 1'b1 ;
  wire    [$clog2(DEPTH)-1:0]   next_rd_ptr = fifo_rd_ptr + 1'b1 ;

  //if read enable and fifo is not empty, increment read pointer
  always @(posedge clk or negedge rst_n) begin
    if(!rst_n)
        fifo_rd_ptr <= 0 ;
    else if (rd_en && (~ empty)) begin       
        data_out <= fifo_mem[fifo_rd_ptr] ;      
        fifo_rd_ptr <= next_rd_ptr;
    end
  end

  //if write enable and fifo is not full, increment write pointer
  always @(posedge clk or negedge rst_n) begin
    if(!rst_n)
        fifo_wr_ptr <= 0 ;
    else if (wr_en && (~ full)) begin       
        fifo_mem[fifo_wr_ptr] <= data_in ;      
        fifo_wr_ptr <= next_wr_ptr;
    end
  end

  //compare read and write pointers to determine if fifo is empty or full
  always @(posedge clk or negedge rst_n) begin    
    if(rst_n == 1'b0) begin
        empty <= 1'b1 ;
        full <= 1'b0 ;
    end
    else begin
        if(wr_en & !rd_en) begin
          empty <= 1'b0;
          full <= (next_wr_ptr == fifo_rd_ptr);
        end
        else if(rd_en & !wr_en) begin
          full  <= 1'b0;
          empty <= (next_rd_ptr == fifo_wr_ptr);
        end
    end
  end

endmodule