import React, { Component } from "react";

import UserService from "../services/user.service";
import EventBus from "../common/EventBus";

export default class BoardUser extends Component {
  constructor(props) {
    super(props);

    this.state = {
      content: ""
    };
  }

  componentDidMount() {
    UserService.getUserBoard().then(
      response => {
        this.setState({
          content: response.data.message,
          products: response.data.products
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

  render() {
    return (
      <div className="container">
        <header className="jumbotron">
          <h3>{this.state.content}</h3>
        </header>
        {Array.isArray(this.state.products) &&  this.state.products.length > 0 ? (
          <div>
            <h4>Products:</h4>
            <ul className="list-group">
              {this.state.products.map((product, index) => (
                <li key={index} className="list-group-item">
                  <div className="d-flex justify-content-between align-items-center">
                    <span>{`Name: ${product.name}`}</span>
                    <span>{`Price: ${product.price}`}</span>
                  </div>
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
