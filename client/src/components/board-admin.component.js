import React, { Component } from "react";

import UserService from "../services/user.service";
import EventBus from "../common/EventBus";
import ProductService from "../services/product.service";


export default class BoardAdmin extends Component {
  constructor(props) {
    super(props);

    this.state = {
      content: ""
    };
  }

  componentDidMount() {
    UserService.getAdminBoard().then(
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

  handleRemove(index) {
    const productId = this.state.products[index].id;

    ProductService.deleteProduct(productId)
      .then(() => {
        const products = [...this.state.products];
        products.splice(index, 1);
        this.setState({ products });
      })
      .catch((error) => {
        console.error("Error removing product:", error);
      });
  }
  handleCreate() {
    ProductService.createProduct({ name: `Product${this.state.products.length + 2}`, price: "Waiting" })
      .then((newProduct) => {
        const products = [...this.state.products, newProduct];
        this.setState({ products });
      })
      .catch((error) => {
        console.error("Error creating product:", error);
      });
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
    const { id, name, price } = this.state.editedProducts[index];

    const updatedData = { name, price };

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
                <div className="d-flex justify-content-between align-items-center">
                  {this.state.editedProducts[index] ? (
                    <div>
                      <input
                        type="text"
                        name="name"
                        value={this.state.editedProducts[index].name || product.name}
                        onChange={(event) => this.handleInputChange(index, event)}
                      />
                      <input
                        type="text"
                        name="price"
                        value={this.state.editedProducts[index].price || product.price}
                        onChange={(event) => this.handleInputChange(index, event)}
                      />
                      <button onClick={() => this.handleUpdate(index)}>Update</button>
                    </div>
                  ) : (
                    <>
                      <span>{`Name: ${product.name}`}</span>
                      <span>{`Price: ${product.price}`}</span>
                      <button onClick={() => this.handleEdit(index)}>Edit</button>
                      <button onClick={() => this.handleRemove(index)}>Remove</button>
                    </>
                  )}
                </div>
              </li>
            ))}
          </ul>
          <button onClick={() => this.handleCreate()}>Create New Product</button>
        </div>
      ) : (
        <p>No products available.</p>
      )}
      </div>
    );
  }
}