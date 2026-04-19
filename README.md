A flexible scaling data structure that holds upto 64 values per row, if initialized with that size. It scales depending on values added, upto around 33~ million rows.
Simply Initialize the structure with the amount of rows you need 3 rows -> new FlexRay(3) -> {val1, val2, val3} structure

getData(10) will retrieve the 10th value of the total count of values.


It scales depending on need and is optimized for calls with CPU and requirement on memory. 

Goals later: 
- Add a de-scale functionality
- Provide consistent data retrieval ( positions shift over time and calling value 1500 may not always be the same value)
- Add Several types (int, string)
- Add similar key-calling to hashmaps for consistent data retrieval
- Get batch data (more than 1 value/call)
