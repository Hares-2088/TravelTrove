import React from "react";
import { Table, Button } from "react-bootstrap";
import { Link } from "react-router-dom";
import { PencilSquare, Trash } from "react-bootstrap-icons";
import { UserResponseModel } from "../model/users.model";

interface UsersListProps {
  users: UserResponseModel[];
}

const UsersList: React.FC<UsersListProps> = ({ users }) => {
  return (
    <Table hover className="align-middle">
      <thead className="table-light">
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.userId}>
            <td>
              <Link
                to={`/users/${user.userId}`}
                className="text-primary"
              >
                {user.firstName} {user.lastName}
              </Link>
            </td>
            <td>{user.email}</td>
            <td>{user.roles}</td>
            <td>
              <Button variant="outline-primary" size="sm" className="me-2">
                <PencilSquare className="me-1" />
                Edit
              </Button>
              <Button variant="outline-danger" size="sm">
                <Trash className="me-1" />
                Delete
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default UsersList;
