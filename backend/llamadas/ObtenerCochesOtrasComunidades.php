<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['IdComunidad'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerCochesOtrasComunidades($_POST['IdUsuario'],$_POST['IdComunidad']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay coches para mostrar.";
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
