<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['IdComunidad'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerCoches($_POST['IdUsuario'],$_POST['IdComunidad']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay comunidades para mostrar.";
                	}
			$resultado2 = $bbdd->obtenerCochesOtrasComunidades($_POST['IdUsuario'],$_POST['IdComunidad']);
			$respuesta['cochesOtrasComunidades'] = $resultado2;

			$resultado3 = $bbdd->obtenerOfertasFuturas($_POST['IdUsuario'],$_POST['IdComunidad']);
                        $respuesta['OfertasFuturas'] = $resultado3;
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
