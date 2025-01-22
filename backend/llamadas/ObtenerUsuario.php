<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdUsuario'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerUsuario($_GET['IdUsuario']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "OK";
                                $respuesta['Usuario'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No se ha encontrado el usuario.";
                }
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
