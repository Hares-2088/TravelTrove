import React from "react"
import { Form, Button } from "react-bootstrap";


type FilterOption ={
    label: string; //Label to display
    value: string; //Current selected value
    options: {id:string; name:string}[]; //list of dropdown options
    onChange:(value:string) => void; //Handler for value changes
    disabled?: boolean // optional: Whether the drop down should be disabled
}

const FilterBar: React.FC<{
    filters: FilterOption[]; // Array of filters
    resetFilters: () => void; // Function to reset all filters
  }> = ({ filters, resetFilters }) => (
    <div className="d-flex gap-2 mb-3">
      {filters.map((filter, index) => (
        <Form.Select
          key={index}
          value={filter.value}
          onChange={(e) => filter.onChange(e.target.value)}
          disabled={filter.disabled}
        >
          <option value="">{filter.label}</option>
          {filter.options.map((option) => (
            <option key={option.id} value={option.id}>
              {option.name}
            </option>
          ))}
        </Form.Select>
      ))}
      <Button variant="outline-secondary" onClick={resetFilters}>
        Reset Filters
      </Button>
    </div>
  );
  
  export default FilterBar;