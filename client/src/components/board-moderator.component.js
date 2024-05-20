import React, { Component } from "react";

import UserService from "../services/user.service";
import EventBus from "../common/EventBus";
import ProductService from "../services/product.service";

export default class BoardModerator extends Component {
  constructor(props) {
    super(props);

    this.state = {
      content: ""
    };
  }

  componentDidMount() {
    UserService.getModeratorBoard().then(
      response => {
        this.setState({
          content: response.data.message,
          products: response.data.products || [],
          editedProducts: Array(response.data.products.length).fill(false)
        });
      },
      error => {
        this.setState({
          content:
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString()
        });

        if (error.response && error.response.status === 401) {
          EventBus.dispatch("logout");
        }
      }
    );
  }

  handleEdit(index) {
    const editedProducts = [...this.state.editedProducts];
    const productToEdit = { ...this.state.products[index] }; // Copy the product to edit
    editedProducts[index] = productToEdit;
    this.setState({ editedProducts });
  }

  handleInputChange(index, event) {
    const editedProducts = [...this.state.editedProducts];
    editedProducts[index] = { ...editedProducts[index], [event.target.name]: event.target.value };
    this.setState({ editedProducts });
  }

  handleUpdate(index) {
    const { id, name, status } = this.state.editedProducts[index];

    const updatedData = { name, status };

    ProductService.updateProduct(id, updatedData)
    .then((updatedProduct) => {
      const products = [...this.state.products];
      products[index] = updatedProduct;

      const editedProducts = [...this.state.editedProducts];
      editedProducts[index] = false;

      this.setState({ products, editedProducts });
    })
    .catch((error) => {
      console.error("Error updating product:", error);
    });
  }


  render() {
    return (
      <div className="container">
        <header className="jumbotron">
          <h3>{this.state.content}</h3>
        </header>
        {Array.isArray(this.state.products) && this.state.products.length > 0 ? (
        <div>
          <h4>Products:</h4>
          <ul className="list-group">
            {this.state.products.map((product, index) => (
              <li key={index} className="list-group-item">
                {this.state.editedProducts[index] ? (
                  <div>
                    <span>{`Name: ${this.state.editedProducts[index].name || product.name}`}</span>
                    <input
                      type="text"
                      name="status"
                      value={this.state.editedProducts[index].status || product.status}
                      onChange={(event) => this.handleInputChange(index, event)}
                    />
                    <button onClick={() => this.handleUpdate(index)}>Update</button>
                  </div>
                ) : (
                  <div className="d-flex justify-content-between align-items-center">
                    <span>{`Name: ${product.name}`}</span>
                    <span>{`Status: ${product.status}`}</span>
                    <button onClick={() => this.handleEdit(index)}>Edit</button>
                  </div>
                )}
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p>No products available.</p>
      )}
      </div>
    );
  }
}
