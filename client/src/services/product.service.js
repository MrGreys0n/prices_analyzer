import axios from "axios";
import authHeader from "./auth-header";

const API_URL = "http://localhost:8080/api/test/";

class ProductService {

    updateProduct(productId, updatedProduct){
        const updateUrl = `${API_URL}products/${productId}`;

        return axios.put(updateUrl, updatedProduct, { headers: authHeader() })
            .then((response) => {
                return response.data;
            })
            .catch((error) => {
                throw error;
            });
    }

    createProduct(newProductData) {
        const createUrl = `${API_URL}products`;

        return axios.post(createUrl, newProductData, { headers: authHeader() })
          .then((response) => {
            const createdProduct = response.data;
            return createdProduct;
          })
          .catch((error) => {
            throw error;
          });
    }

      deleteProduct(productId) {
        const deleteUrl = `${API_URL}products/${productId}`;
        console.log(authHeader());
        return axios.delete(deleteUrl, { headers: authHeader() })
          .then(() => {
            return { message: 'Product deleted successfully' };
          })
          .catch((error) => {
            throw authHeader();
            // throw error;
          });
    }
}

export default new ProductService();
