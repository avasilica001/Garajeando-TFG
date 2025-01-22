<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['IdComunidad'])){
                        $bbdd = new DBOperations();

                        if($bbdd->aceptarUsuarioAComunidad($_POST['IdUsuario'],$_POST['IdComunidad'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "El usuario se ha aceptado en la comunidad.";
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "El usuario no se ha podido aceptar en la comunidad.";
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
