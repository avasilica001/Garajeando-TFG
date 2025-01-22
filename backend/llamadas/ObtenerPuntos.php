<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdUsuario'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerPuntos($_GET['IdUsuario']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "OK";
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay puntos.";
                        }

                        $respuesta['puntos'] = $resultado;
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
