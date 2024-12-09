import React from "react";
import { Form } from "react-bootstrap";
import { EventRequestModel } from "../../events/model/models";

interface EventFormProps {
  formData: EventRequestModel;
  setFormData: React.Dispatch<React.SetStateAction<EventRequestModel>>;
  handleSaveEvent: () => void;
  modalType: "create" | "update";
}

const EventForm: React.FC<EventFormProps> = ({ formData, setFormData, handleSaveEvent, modalType }) => {
  return (
    <Form>
      <Form.Group className="mb-3">
        <Form.Label>Event Name</Form.Label>
        <Form.Control
          type="text"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Label>Event Date</Form.Label>
        <Form.Control
          type="date"
          value={formData.date}
          onChange={(e) => setFormData({ ...formData, date: e.target.value })}
        />
      </Form.Group>
      <Form.Group className="mb-3">
        <Form.Label>Event Description</Form.Label>
        <Form.Control
          as="textarea"
          value={formData.description}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          rows={3}
        />
      </Form.Group>
      <button type="button" className="btn btn-primary" onClick={handleSaveEvent}>
        {modalType === "create" ? "Create Event" : "Update Event"}
      </button>
    </Form>
  );
};

export default EventForm;
