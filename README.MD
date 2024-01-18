# Soporte de motoristas

Este proyecto surge con la finalidad inicial de gestionar motoristas y pedidos, pero ha evolucionado al incorporar un módulo adicional que facilita la entrada de órdenes de manera ágil. Este módulo presenta un botón que despliega una ventana, la cual contiene un área de texto donde se pueden ingresar las órdenes.

## Formato de Órdenes

El formato de las órdenes se estructura de la siguiente manera:

```plaintext
{headerStarted}
Nombre: {nameCustomer}
Teléfono: {phoneNumber}
Ciudad: {city}
Departamento: {department}
Referencia: {referenceDeliveryProduct}
Productos: ({amountInteger}{nameProduct} + {amountInteger}{nameProduct}).iterable
Total a pagar: {total}
Envío: {deliveryPrice}
Fecha de entrega: {dateToDelivery}
Comentario: {comment}
```

En este formato, se facilita la introducción de múltiples órdenes mediante la acción de copiar y pegar el texto. Un aspecto clave es `{headerStarted}`, el cual señala al programa el inicio de un nuevo bloque de texto de orden. Por defecto, el programa utiliza `Confirmando los datos de su pedido` como encabezado; si quieres puedes modificar este header según las necesidades del usuario en el archivo `src\main\java\decoder\core\DecoderMultipleOrders.java`.

La flexibilidad de este formato permite gestionar una cantidad variable de productos en cada orden. Es importante seguir el formato proporcionado: `{amountInteger}` es la cantidad del producto solicitado. En caso de no especificarse este número, el programa asumirá que se trata de un solo producto.

## Posibles Problemas

El programa actualmente no es totalmente resistente, ya que carece de validaciones ortográficas. Por ejemplo, si el usuario escribe `referenca` en lugar de `referencia`, el programa puede experimentar problemas. Se recomienda validar que todo esté en orden antes de generar la orden.

¿Cómo validar que todo está correcto antes de crear la orden? Utiliza el botón "Scanear" para revisar y corregir el texto antes de proceder. Este recurso te ayudará a identificar y corregir posibles errores, ya sea desde otra fuente o directamente en el área de escaneo.



# To-Do List

- [ ] **El scanner en grande**
- [ ] **Preview de la orden estilo ticket a un lado del scanner**
- [ ] **Mostrar una ventana de scanner a la vez**
- [ ] **Validar si el producto existe**
- [ ] **Mostrar en checkbox los productos**
- [X] **En algoritmo de porcentaje de coincidencia agregar siguientes filtros:**
    ```sql
    silverpos.plus p  where activo 
    1 and length(nombre)>0 and id not in (170,171,173,180,100); 
    ```
- [ ] **Los comentarios y la fecha de entrega, buscar dónde deben ser insertados**
- [ ] **Revisar qué keys son necesarias al hacer los insert, y pasar las que no sean necesarias como ""**